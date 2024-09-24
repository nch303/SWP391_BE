package koicare.koiCareProject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Member")
@Data
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberid")
    private long memberID;
    private String memberName;
    private String memberEmail;
    private String memberPhone;
    private int premiumStatus;
    private Date expiredDate;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "accountid", referencedColumnName = "accountid")
//
//    private Account account;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Pond> ponds;



}
