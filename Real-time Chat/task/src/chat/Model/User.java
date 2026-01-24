package chat.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {


    private Long id;
    private String username;
    private LocalDateTime timestamp;

    public User(String username) {
        this.username = username;
        this.timestamp = LocalDateTime.now();
    }


}
