package extension.filter.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_extension")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomExtension extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "extension", nullable = false, unique = true, length = 20)
    private String extension;

    public static CustomExtension create(String extension) {
        return CustomExtension.builder()
                .extension(extension)
                .build();
    }
}
