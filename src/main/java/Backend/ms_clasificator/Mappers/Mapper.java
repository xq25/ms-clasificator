package Backend.ms_clasificator.Mappers;

public interface Mapper<Entity,DTO> {
    Entity toEntity(DTO dto);
    DTO toDTO(Entity entity);

}
