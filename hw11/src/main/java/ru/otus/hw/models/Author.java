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
@Table(name = "authors")
@ToString
@EqualsAndHashCode
public class Author {
    @Id
    @Column("id")
    private Long id;

    @Column("full_name")
    private String fullName;

    @PersistenceCreator
    public Author(Long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }
}
