package com.mays.srm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusCountDTO {
    private long total;
    private long inward;
    private long outward;
    private long enquiry;
    private long ticketStatusCheck;
    private long others;
}
