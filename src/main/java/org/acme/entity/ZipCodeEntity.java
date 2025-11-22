package org.acme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "zip_code")
public class ZipCodeEntity {

  @Id
  private String zip;
  private String city;
  private String county;
  private String state;
  private String timezone;
  private String type;

  public ZipCodeEntity() {
  }

  public ZipCodeEntity(String zip, String city, String county, String state, String timezone, String type) {
    this.zip = zip;
    this.city = city;
    this.county = county;
    this.state = state;
    this.timezone = timezone;
    this.type = type;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCounty() {
    return county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}