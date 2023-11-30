package hc.gras;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AckM {
    private Integer type;
    private Long  userId;
    private String command;
    private List<Long> seqIds;
}
