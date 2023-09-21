package d209.Idontcare.mission.dto;


import d209.Idontcare.mission.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
public class MissionDto {

    private Long parentId;

    private Long childId;

    private String title;

    private Long amount;

    private Type type;

    private String beforeMessage;

    private String afterMessage;

}
