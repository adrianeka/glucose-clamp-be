package com.tujuhsembilan.bookrecipe.service;

import com.tujuhsembilan.bookrecipe.model.Levels;
import com.tujuhsembilan.bookrecipe.repository.LevelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LevelsService {

    @Autowired
    private LevelsRepository levelsRepository;

    public List<Levels> getAllLevels() {
        return levelsRepository.findAll();
    }
}
