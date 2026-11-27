package reclaim.model;

import reclaim.exceptions.ItemAlreadyClaimedException;

/** Something that an owner can claim back after verifying ownership. */
public interface Claimable {
    boolean verifyClaim(String answer);

    void markClaimed(String claimantName) throws ItemAlreadyClaimedException;

    boolean isClaimed();
}
