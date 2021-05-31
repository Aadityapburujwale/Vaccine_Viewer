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
