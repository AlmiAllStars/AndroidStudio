package com.almi.juegaalmiapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> surname = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> phone = new MutableLiveData<>();
    private final MutableLiveData<String> address = new MutableLiveData<>();
    private final MutableLiveData<String> postalCode = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();

    // Getters
    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getSurname() {
        return surname;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPhone() {
        return phone;
    }

    public LiveData<String> getAddress() {
        return address;
    }

    public LiveData<String> getPostalCode() {
        return postalCode;
    }

    public LiveData<String> getPassword() {
        return password;
    }

    // Setters
    public void setName(String newName) {
        name.setValue(newName);
    }

    public void setSurname(String newSurname) {
        surname.setValue(newSurname);
    }

    public void setEmail(String newEmail) {
        email.setValue(newEmail);
    }

    public void setPhone(String newPhone) {
        phone.setValue(newPhone);
    }

    public void setAddress(String newAddress) {
        address.setValue(newAddress);
    }

    public void setPostalCode(String newPostalCode) {
        postalCode.setValue(newPostalCode);
    }

    public void setPassword(String newPassword) {
        password.setValue(newPassword);
    }
}
