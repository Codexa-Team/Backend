package com.codexateam.platform.reviews.domain.model;

import com.codexateam.platform.reviews.domain.model.commands.CreateReviewCommand;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewByIdQuery;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewsByRenterIdQuery;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewsByVehicleIdQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewRecordsTest {

    @Test
    @DisplayName("CreateReviewCommand record preserves values and equals/hashCode contract (AAA)")
    void createReviewCommand_ShouldPreserveValuesAndEquality() {
        // Arrange & Act
        var cmd1 = new CreateReviewCommand(10L, 2L, 5, "Great!");
        var cmd2 = new CreateReviewCommand(10L, 2L, 5, "Great!");

        // Assert
        assertEquals(10L, cmd1.vehicleId());
        assertEquals(2L, cmd1.renterId());
        assertEquals(5, cmd1.rating());
        assertEquals("Great!", cmd1.comment());
        assertEquals(cmd1, cmd2);
        assertEquals(cmd1.hashCode(), cmd2.hashCode());
    }

    @Test
    @DisplayName("GetReviewsByVehicleIdQuery record encapsulates vehicleId (AAA)")
    void getReviewsByVehicleIdQuery_ShouldEncapsulateVehicleId() {
        // Arrange & Act
        var query = new GetReviewsByVehicleIdQuery(42L);

        // Assert
        assertEquals(42L, query.vehicleId());
    }

    @Test
    @DisplayName("GetReviewsByRenterIdQuery record encapsulates renterId (AAA)")
    void getReviewsByRenterIdQuery_ShouldEncapsulateRenterId() {
        // Arrange & Act
        var query = new GetReviewsByRenterIdQuery(15L);

        // Assert
        assertEquals(15L, query.renterId());
    }

    @Test
    @DisplayName("GetReviewByIdQuery record encapsulates reviewId (AAA)")
    void getReviewByIdQuery_ShouldEncapsulateReviewId() {
        // Arrange & Act
        var query = new GetReviewByIdQuery(88L);

        // Assert
        assertEquals(88L, query.reviewId());
    }
}
