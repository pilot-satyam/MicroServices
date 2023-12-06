package com.lcwd.user.service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class GroupUser {
    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String groupName;

    public GroupUser(){}

    public GroupUser(String id,String groupName){
        this.id = id;
        this.groupName = groupName;
    }

    public  String getId(){
        return id;
    }
    public  void setId(String id){
        this.id = id;
    }
    public String getName(){
        return groupName;
    }
    public  void setName(String groupName){
        this.groupName = groupName;
    }
}
