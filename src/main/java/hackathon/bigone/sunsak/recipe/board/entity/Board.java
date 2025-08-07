package hackathon.bigone.sunsak.recipe.board.entity;

import hackathon.bigone.sunsak.accounts.user.entity.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import hackathon.bigone.sunsak.recipe.board.enums.RecipeCategory;
import hackathon.bigone.sunsak.accounts.user.entity.SiteUser;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
@Table(name = "RecipeBoards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable=false, length = 100)
    private String title;

    @ManyToOne
    @JoinColumn(name = "username")
    private SiteUser author;


    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Step> steps = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private RecipeCategory category;


}
