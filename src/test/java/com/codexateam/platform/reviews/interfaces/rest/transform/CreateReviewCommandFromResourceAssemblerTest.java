package com.codexateam.platform.reviews.interfaces.rest.transform;

import com.codexateam.platform.reviews.domain.model.aggregates.Review;
import com.codexateam.platform.reviews.domain.model.commands.CreateReviewCommand;
import com.codexateam.platform.reviews.interfaces.rest.resources.CreateReviewResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateReviewCommandFromResourceAssemblerTest {

    @Test
    @DisplayName("toCommandFromResource should transform CreateReviewResource into CreateReviewCommand (AAA)")
    void toCommandFromResource_ShouldTransformSuccessfully() {
        // Arrange
        var resource = new CreateReviewResource(10L, 5, "Remarkable car!");

        // Act
        var command = CreateReviewCommandFromResourceAssembler.toCommandFromResource(resource, 4L);

        // Assert
        assertNotNull(command);
        assertEquals(10L, command.vehicleId());
        assertEquals(4L, command.renterId());
        assertEquals(5, command.rating());
        assertEquals("Remarkable car!", command.comment());
    }

    @Test
    @DisplayName("ReviewResourceFromEntityAssembler should transform Review aggregate into ReviewResource (AAA)")
    void toResourceFromEntity_ShouldTransformSuccessfully() {
        // Arrange
        var command = new CreateReviewCommand(10L, 4L, 5, "Remarkable car!");
        var review = new Review(command);

        // Act
        var resource = ReviewResourceFromEntityAssembler.toResourceFromEntity(review);

        // Assert
        assertNotNull(resource);
        assertEquals(10L, resource.vehicleId());
        assertEquals(4L, resource.renterId());
        assertEquals(5, resource.rating());
        assertEquals("Remarkable car!", resource.comment());
    }
}
