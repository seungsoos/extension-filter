package extension.filter.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fixed_extension")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FixedExtension extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "extension", nullable = false, unique = true, length = 20)
    private String extension;

    @Column(name = "is_checked", nullable = false)
    private Boolean isChecked = false;

    public static FixedExtension create(String extension) {
        return FixedExtension.builder()
                .extension(extension)
                .isChecked(false)
                .build();
    }

    public void updateChecked(Boolean checked) {
        this.isChecked = checked;
    }
}
