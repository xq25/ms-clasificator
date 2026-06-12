package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document", nullable = false, unique = true)
    private String document;

    @Column(name = "dob", nullable = false) // Date Of Birthday
    private Date dob;

    @Column(name = "sex", nullable = false)
    private String sex;

    @Column(name="user_id", nullable = false, unique = true)
    private String userId;

}