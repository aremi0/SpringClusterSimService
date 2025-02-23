package com.aremi.springclustersimservice.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TaskResult {
    private final boolean isSuccess;
    private final int executionTime;
}
