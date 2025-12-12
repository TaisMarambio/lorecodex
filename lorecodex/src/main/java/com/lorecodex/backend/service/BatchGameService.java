package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.request.BatchGameRequest;
import com.lorecodex.backend.dto.response.BatchGameResponse;

public interface BatchGameService {
    BatchGameResponse importGamesInBatch(BatchGameRequest request);
}
