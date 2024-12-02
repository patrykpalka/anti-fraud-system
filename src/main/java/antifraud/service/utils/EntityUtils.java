package antifraud.service.utils;

import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.RemovableEntity;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class EntityUtils {
    /**
     * Adds a new entity to the system after performing necessary validations.
     *
     * @param requestDTO        the data transfer object containing the information of the entity to add
     * @param toEntity          a function to convert the request DTO into the corresponding entity
     * @param findEntityByField a function to find an existing entity by a specific field (e.g., IP or card number)
     * @param saveEntity        a consumer to save the new entity in the repository
     * @param entityType        the type of entity being added (e.g., "IP address" or "card number")
     * @param <T>               the type of the entity to be added
     * @param <R>               the type of the request DTO
     * @return the created entity wrapped in a {@link ResponseEntity}
     * @throws BadRequestException if the field is invalid or missing
     * @throws ConflictException   if an entity with the specified field already exists
     * @throws IllegalArgumentException if the DTO type is unsupported
     */
    public static <T, R> ResponseEntity<T> addEntity(R requestDTO, Function<R, T> toEntity,
            Function<String, Optional<T>> findEntityByField, Consumer<T> saveEntity, String entityType) {
        String field = getFieldFromDTO(requestDTO, dto -> {
            if (dto instanceof SuspiciousIpRequestDTO) return ((SuspiciousIpRequestDTO) dto).getIp();
            if (dto instanceof StolenCardRequestDTO) return ((StolenCardRequestDTO) dto).getNumber();
            throw new IllegalArgumentException("Unsupported DTO type");
        });

        if (findEntityByField.apply(field).isPresent()) {
            throw new ConflictException("This " + entityType + " is already in use");
        }

        T entity = toEntity.apply(requestDTO);
        saveEntity.accept(entity);

        return ResponseEntity.ok(entity);
    }

    /**
     * Extracts a field value from the given DTO using the provided field extractor function.
     *
     * @param dto               the data transfer object from which to extract the field value
     * @param fieldExtractor    a function that extracts the field value from the DTO
     * @return the extracted field value
     * @param <R>               the type of the DTO
     */
    private static <R> String getFieldFromDTO(R dto, Function<R, String> fieldExtractor) {
        return fieldExtractor.apply(dto);
    }

    /**
     * Removes an entity from the system after performing necessary validations.
     *
     * @param field             the field value used to find the entity to be removed
     * @param findEntityByField       a function to find an existing entity by the specified field
     * @param deleteEntity      a consumer to delete the entity from the repository
     * @param entityType        the type of entity being removed (e.g., "IP address" or "card number")
     * @param <T>               the type of the removable entity
     * @return a {@link ResponseEntity} containing the response data after the entity is deleted
     * @throws BadRequestException if the field is invalid or missing
     * @throws NotFoundException   if the specified entity is not found
     */
    public static <T extends RemovableEntity> ResponseEntity<AntiFraudDeletionResponseDTO<T>> removeEntity(
            String field, Function<String, Optional<T>> findEntityByField, Consumer<T> deleteEntity, String entityType) {
        T entity = findEntityByField.apply(field)
                .orElseThrow(() -> new NotFoundException("The specified " + entityType + " (" + field + ") was not found."));

        deleteEntity.accept(entity);

        return ResponseEntity.ok(new AntiFraudDeletionResponseDTO<>(entity));
    }
}
