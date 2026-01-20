package chat.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
