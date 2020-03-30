package edu.ucsb.cs48.s20.demo.entities;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String uid;
    private String login;

   
    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public AppUser() {
    }

    public AppUser(String uid, String login) {
        this.uid = uid;
        this.login = login;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + id + "'" +
            ", uid='" + uid + "'" +
            ", login='" + login + "'" +
            "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AppUser)) {
            return false;
        }
        AppUser appUser = (AppUser) o;
        return id == appUser.id && Objects.equals(uid, appUser.uid) && Objects.equals(login, appUser.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uid, login);
    }
  
}
