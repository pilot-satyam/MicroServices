package com.lcwd.user.service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupsOkta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int Id;
    private String GroupName;
    private String Attribute;
    private String Path;
    private String type;
    private String RoleName;

    @ManyToMany
    @JoinTable(
        name = "group_roles",
        joinColumns = @JoinColumn(name = "groupsokta_id"),
        inverseJoinColumns = @JoinColumn(name="roles_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Roles> roles = new HashSet<>();
}
