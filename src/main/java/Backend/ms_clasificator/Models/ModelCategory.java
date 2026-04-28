package Backend.ms_clasificator.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "model_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "modelCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IAModel> iaModels;
}