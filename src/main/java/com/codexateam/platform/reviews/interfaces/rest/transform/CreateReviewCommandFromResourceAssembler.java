package com.codexateam.platform.reviews.interfaces.rest.transform;

import com.codexateam.platform.reviews.domain.model.commands.CreateReviewCommand;
import com.codexateam.platform.reviews.interfaces.rest.resources.CreateReviewResource;

/**
 * Assembler to convert CreateReviewResource DTO to CreateReviewCommand.
 */
public class CreateReviewCommandFromResourceAssembler {
    /**
     * Converts a CreateReviewResource DTO to a CreateReviewCommand.
     * @param resource The input DTO.
     * @param renterId The ID of the authenticated renter (from security context).
     * @return The CreateReviewCommand.
     */
    public static CreateReviewCommand toCommandFromResource(CreateReviewResource resource, Long renterId) {
        return new CreateReviewCommand(
                resource.vehicleId(),
                renterId,
                resource.rating(),
                resource.comment()
        );
    }
}
