package com.example.vaccineviewer;

public class vaccinationData {

    String centerName;
    String centerAddress;
    String vaccinationStartTime;
    String VaccinationEndTime;
    String vaccinePrice;
    String vaccineName;
    int totalAvailableVaccin;
    int minimumAgeLimit;

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getCenterAddress() {
        return centerAddress;
    }

    public void setCenterAddress(String centerAddress) {
        this.centerAddress = centerAddress;
    }

    public String getVaccinationStartTime() {
        return vaccinationStartTime;
    }

    public void setVaccinationStartTime(String vaccinationStartTime) {
        this.vaccinationStartTime = vaccinationStartTime;
    }

    public String getVaccinationEndTime() {
        return VaccinationEndTime;
    }

    public void setVaccinationEndTime(String vaccinationEndTime) {
        VaccinationEndTime = vaccinationEndTime;
    }

    public String getVaccinePrice() {
        return vaccinePrice;
    }

    public void setVaccinePrice(String vaccinePrice) {
        this.vaccinePrice = vaccinePrice;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public int getTotalAvailableVaccin() {
        return totalAvailableVaccin;
    }

    public void setTotalAvailableVaccin(int totalAvailableVaccin) {
        this.totalAvailableVaccin = totalAvailableVaccin;
    }

    public int getMinimumAgeLimit() {
        return minimumAgeLimit;
    }

    public void setMinimumAgeLimit(int minimumAgeLimit) {
        this.minimumAgeLimit = minimumAgeLimit;
    }

    public vaccinationData(String centerName, String centerAddress, String vaccinationStartTime, String vaccinationEndTime, String vaccinePrice, String vaccineName, int totalAvailableVaccin, int minimumAgeLimit) {
        this.centerName = centerName;
        this.centerAddress = centerAddress;
        this.vaccinationStartTime = vaccinationStartTime;
        this.VaccinationEndTime = vaccinationEndTime;
        this.vaccinePrice = vaccinePrice;
        this.vaccineName = vaccineName;
        this.totalAvailableVaccin = totalAvailableVaccin;
        this.minimumAgeLimit = minimumAgeLimit;
    }
}
