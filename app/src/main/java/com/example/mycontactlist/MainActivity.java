package com.example.mycontactlist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements com.example.mycontactlist.DatePicker.SaveDateListener {

    //hi

    private Contact currentContact;
    final int PERMISSION_REQUEST_PHONE = 102;
    final int PERMISSION_REQUEST_CAMERA = 103;
    final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initChangeDateButton();
        initListButton();
        initSettingsButton();
        initMapButton();
        initSaveButton();
        initToggleButton();
        initCallFunction();
        initTextChangedEvents();
        initImageButton();
        setForEditing(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initContact(extras.getInt("contactId"));
        } else {
            currentContact = new Contact();
        }
    }

    private void initCallFunction() {
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        editPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkPhonePermission(currentContact.getPhoneNumber());
                return false;
            }
        });

        EditText editCell = (EditText) findViewById(R.id.editCell);
        editCell.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkPhonePermission(currentContact.getCellNumber());
                return false;
            }
        });

    }

    private void checkPhonePermission(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.CALL_PHONE)) {

                    Snackbar.make(findViewById(R.id.activity_main),
                                    "Calendar requires this permission to place a call from the app.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(
                                            MainActivity.this,
                                            new String[]{
                                                    Manifest.permission.CALL_PHONE},
                                            PERMISSION_REQUEST_PHONE);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]
                                    {Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST_PHONE);
                }
            } else {
                callContact(phoneNumber);
            }
        } else {
            callContact(phoneNumber);
        }

    }


    public void onRequestPermissionResult(int requestCode, @NonNull String permissions[],
                                          @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "You may now call from this app", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "You will not be able to make calls from this app", Toast.LENGTH_LONG).show();
                }
            }

            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(MainActivity.this, "You will not be able to save contact pictures from this app", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void callContact(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        //URI = uniform resource identifier.
        //Used to identify a local resource like a URL
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            startActivity(intent);
        }
    }

    private void initImageButton() {
        ImageButton ib = findViewById(R.id.imageContact);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                Manifest.permission.CAMERA)) {

                            Snackbar.make(findViewById(R.id.activity_main),
                                            "The app needs permission to take a picture.",
                                            Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            ActivityCompat.requestPermissions

                                                    (MainActivity.this, new String[]
                                                                    { Manifest.permission.CAMERA},
                                                    PERMISSION_REQUEST_CAMERA);
                                        }
                                    })
                                    .show();
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                                            {Manifest.permission.CAMERA},
                                    PERMISSION_REQUEST_CAMERA);
                        }
                        }
                     else {
                        takePhoto();
                    }
                } else {
                    takePhoto();
                }
            }
        });
    }

    public void takePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //returns value to app after it was completed
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    //captures the photo
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 144, 144, true);
                ImageButton imageContact = (ImageButton) findViewById(R.id.imageContact);
                imageContact.setImageBitmap(scaledPhoto);
                currentContact.setPicture(scaledPhoto);
            }
        }
    }



    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        TextView birthDay = findViewById(R.id.textBirthday);
        birthDay.setText(DateFormat.format("MM/dd/yyyy", selectedTime));
        currentContact.setBirthday(selectedTime);
    }

    private void initChangeDateButton() {
        Button birthdayButton = findViewById(R.id.btnBirthday);
        birthdayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DatePicker datePicker = new DatePicker();
                datePicker.show(fm, "DatePick");
            }
        });
    }


    private void initListButton() {
        ImageButton imageListButton = findViewById(R.id.imageButtonList);
        imageListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
        private void initMapButton () {
            ImageButton imageMapButton = findViewById(R.id.imageButtonMap);
            imageMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ContactMapActivity.class);
                        if (currentContact.getContactId() == -1) {
                            Toast.makeText(getBaseContext(), "Contact must be saved before it can be mapped", Toast.LENGTH_LONG).show();
                        }
                        else {
                            intent.putExtra("contactid", currentContact.getContactId());
                        }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }

        private void initSettingsButton () {
                ImageButton imageSettingsButton = findViewById(R.id.imageButtonSettings);
                imageSettingsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, ContactSettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }

            private void initToggleButton() {
                final ToggleButton editToggle = findViewById(R.id.toggleButtonEdit);
                editToggle.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        setForEditing(editToggle.isChecked());
                    }
                });
            }

            private void initTextChangedEvents() {
                final EditText eContactName = findViewById(R.id.editName);
                eContactName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        currentContact.setContactName(eContactName.getText().toString());
                    }
                });

                final EditText eStreetAddress = findViewById(R.id.editAddress);
                eStreetAddress.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        currentContact.setStreetAddress(eStreetAddress.getText().toString());
                    }
                });

                final EditText eState = findViewById(R.id.editState);
                eState.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        currentContact.setState(eState.getText().toString());
                    }
                });

                final EditText eEmail = findViewById(R.id.editEMail);
                eEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        currentContact.seteMail(eEmail.getText().toString());
                    }
                });

                final EditText eHomeNumber = findViewById(R.id.editHome);
                eHomeNumber.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        currentContact.setPhoneNumber(eHomeNumber.getText().toString());
                    }
                });

                final EditText eCellNumber = findViewById(R.id.editCell);
                eCellNumber.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        currentContact.setCellNumber(eCellNumber.getText().toString());
                    }
                });

                final EditText eCity = findViewById(R.id.editCity);
                eCity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        currentContact.setCity(eCity.getText().toString());
                    }
                });

                final EditText eZipCode = findViewById(R.id.editZipCode);
                eZipCode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        currentContact.setZipCode(eZipCode.getText().toString());
                    }
                });

                eHomeNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
                eCellNumber.addTextChangedListener((new PhoneNumberFormattingTextWatcher()));
            }

            private void initSaveButton() {
                Button saveButton = findViewById(R.id.buttonSave);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean wasSuccessful;
                        hideKeyboard();
                        ContactDataSource ds = new ContactDataSource(MainActivity.this);

                        try {
                            ds.open();

                            if (currentContact.getContactId() == -1) {
                                wasSuccessful = ds.insertContact(currentContact);
                            }

                            else {
                                wasSuccessful = ds.updateContact(currentContact);
                            }

                            ds.close();
                        }

                        catch (Exception e) {
                            wasSuccessful = false;
                        }

                        if (wasSuccessful) {
                            ToggleButton editToggle = findViewById(R.id.toggleButtonEdit);
                            editToggle.toggle();
                            setForEditing(false);
                        }

                        if (wasSuccessful) {
                            int newId = ds.getLastContactID();
                            currentContact.setContactId(newId);
                        }
                    }
                });
            }

            private void hideKeyboard() {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                EditText editName = findViewById(R.id.editName);
                imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);

                EditText editAddress = findViewById(R.id.editAddress);
                imm.hideSoftInputFromWindow(editAddress.getWindowToken(), 0);

                EditText editHome = findViewById(R.id.editHome);
                imm.hideSoftInputFromWindow(editHome.getWindowToken(), 0);

                EditText editCell = findViewById(R.id.editCell);
                imm.hideSoftInputFromWindow(editCell.getWindowToken(), 0);

                EditText editZipCode = findViewById(R.id.editZipCode);
                imm.hideSoftInputFromWindow(editZipCode.getWindowToken(), 0);

                EditText editEmail = findViewById(R.id.editEMail);
                imm.hideSoftInputFromWindow(editEmail.getWindowToken(), 0);

                EditText editCity = findViewById(R.id.editCity);
                imm.hideSoftInputFromWindow(editCity.getWindowToken(), 0);

                EditText editState = findViewById(R.id.editState);
                imm.hideSoftInputFromWindow(editState.getWindowToken(), 0);
            }

            private void setForEditing(boolean enabled) {
                EditText editName = findViewById(R.id.editName);
                EditText editAddress = findViewById(R.id.editAddress);
                EditText editCity = findViewById(R.id.editCity);
                EditText editZipcode = findViewById(R.id.editZipCode);
                EditText editState = findViewById(R.id.editState);
                EditText editCell = findViewById(R.id.editCell);
                EditText editEmail = findViewById(R.id.editEMail);
                EditText editHome = findViewById(R.id.editHome);
                Button buttonChange = findViewById(R.id.btnBirthday);
                Button buttonSave = findViewById(R.id.buttonSave);
                ImageButton picture = findViewById(R.id.imageContact);

                editAddress.setEnabled(enabled);
                editState.setEnabled(enabled);
                editName.setEnabled(enabled);
                editZipcode.setEnabled(enabled);
                editEmail.setEnabled(enabled);
                editCity.setEnabled(enabled);
                buttonSave.setEnabled(enabled);
                buttonChange.setEnabled(enabled);
                picture.setEnabled(enabled);

                if (enabled) {
                    editName.requestFocus();
                    editHome.setInputType(InputType.TYPE_CLASS_PHONE);
                    editCell.setInputType(InputType.TYPE_CLASS_PHONE);

                }
                else {
                    editHome.setInputType(InputType.TYPE_CLASS_PHONE);
                    editCell.setInputType(InputType.TYPE_CLASS_PHONE);

                }
            }

            private void initContact(int id) {

                ContactDataSource ds = new ContactDataSource(MainActivity.this);
                try {
                    ds.open();
                    currentContact = ds.getSpecificContact(id);
                    ds.close();
                }
                catch (Exception e) {
                    Toast.makeText(this, "Load Contact Failed", Toast.LENGTH_LONG).show();
                }


                EditText editName = findViewById(R.id.editName);
                EditText editAddress = findViewById(R.id.editAddress);
                EditText editState = findViewById(R.id.editState);
                EditText editCity = findViewById(R.id.editCity);
                EditText editZipcode = findViewById(R.id.editZipCode);
                EditText editCell = findViewById(R.id.editCell);
                EditText editPhone = findViewById(R.id.editHome);
                EditText editEmail = findViewById(R.id.editEMail);
                TextView editBirthday = findViewById(R.id.textBirthday);
                ImageButton picture = (ImageButton) findViewById(R.id.imageContact);

                editPhone.setText(currentContact.getPhoneNumber());
                editAddress.setText(currentContact.getStreetAddress());
                editName.setText(currentContact.getContactName());
                editCity.setText(currentContact.getCity());
                editEmail.setText(currentContact.geteMail());
                editZipcode.setText(currentContact.getZipCode());
                editCell.setText(currentContact.getCellNumber());
                editState.setText(currentContact.getState());
                editBirthday.setText(DateFormat.format("MM/dd/yyy",
                        currentContact.getBirthday().getTimeInMillis()).toString());
                if (currentContact.getPicture() != null) {
                    picture.setImageBitmap(currentContact.getPicture());
                }

                else {

                }


            }



    }

