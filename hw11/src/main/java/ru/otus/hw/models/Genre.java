package ru.otus.hw.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "genres")
@ToString
@EqualsAndHashCode
public class Genre {

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @PersistenceCreator
    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
