package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndOwnerId(Long itemId, Long ownerId);

    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    @Query(value = "select * " +
            "from items as i " +
            "where i.available = true " +
            "and (lower(i.description) like concat('%',:keyword,'%') " +
            "or lower(i.name) like concat('%',:keyword,'%'))",
            nativeQuery = true)
    List<Item> searchItems(@Param("keyword") String keyword);
}
