package explore.with.me.comment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import explore.with.me.event.model.Event;
import explore.with.me.user.model.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "COMMENTS")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text", nullable = false)
    private String text;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User commentator;
    @Column(name = "created_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime editDate;

    public Comment(Long id, String text, Event event, User commentator, LocalDateTime createdDate) {
        this.id = id;
        this.text = text;
        this.event = event;
        this.commentator = commentator;
        this.createdDate = createdDate;
    }
}
