package Backend.ms_clasificator.Mappers;

// Esta clase mapper, permite, un DTO de entrada de datos, una entidad y un DTO de salida de datos o presentacion
public interface Mapper<Entity, DTO, DTOResponse, DTOSummary> {
    Entity toEntity(DTO dto);
    DTO toDTO(Entity entity);
    DTOResponse toResponseDTO(Entity entity);
    DTOSummary toSummaryDTO(Entity entity);
}