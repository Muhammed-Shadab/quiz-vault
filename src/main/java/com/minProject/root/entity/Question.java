package com.minProject.root.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Embeddable
public class Question {
    private String question;
    private List<String> options;
    private String answer;
}