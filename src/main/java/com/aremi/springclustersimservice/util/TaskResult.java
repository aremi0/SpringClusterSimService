package com.aremi.springclustersimservice.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Classe di utilità che contiene il risultato di un task
 */

@Getter
@RequiredArgsConstructor
public class TaskResult {
    private final boolean isSuccess;
    private final long executionTime;
}
