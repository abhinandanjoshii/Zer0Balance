package com.consoleadmin.zer0balance.service;

import com.consoleadmin.zer0balance.dto.ExpenseDTO;
import com.consoleadmin.zer0balance.dto.IncomeDTO;
import com.consoleadmin.zer0balance.entity.CategoryEntity;
import com.consoleadmin.zer0balance.entity.ExpenseEntity;
import com.consoleadmin.zer0balance.entity.IncomeEntity;
import com.consoleadmin.zer0balance.entity.ProfileEntity;
import com.consoleadmin.zer0balance.repository.CategoryRepository;
import com.consoleadmin.zer0balance.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository expenseRepository;
    private final ProfileService profileService;
    private final IncomeRepository incomeRepository;

    public IncomeDTO addIncome(IncomeDTO dto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        IncomeEntity newExpense = toEntity(dto, profile, category);
        incomeRepository.save(newExpense);
        return toDTO(newExpense);
    }

    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        // day 1 to current date
        LocalDate currentDate = now.withDayOfMonth(1);
        LocalDate endDate = now;
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), currentDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    public void deleteIncome(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        //check for that it is for current user
        if(!entity.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("You are not authorized to delete this income");
        }
        incomeRepository.delete(entity);
    }

    public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalIncomeForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalIncomesByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    // helper methods
    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity entity) {
        return IncomeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId(): null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName(): "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
