package com.webapp.demo_app.model;


import com.webapp.demo_app.model.enums.EmployeeeTitle;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //<----------------------------------->

    // for login page

    private String name;
    private String password;

    //<----------------------------------->

    @Enumerated(EnumType.STRING)
    private EmployeeeTitle title;


    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MevcutIs> mevcutIsler;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TamamlananIs> tamamlananIsler;
}
