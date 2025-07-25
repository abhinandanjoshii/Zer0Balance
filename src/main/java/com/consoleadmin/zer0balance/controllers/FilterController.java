package com.consoleadmin.zer0balance.controllers;

import com.consoleadmin.zer0balance.dto.ExpenseDTO;
import com.consoleadmin.zer0balance.dto.FilterDTO;
import com.consoleadmin.zer0balance.dto.IncomeDTO;
import com.consoleadmin.zer0balance.service.ExpenseService;
import com.consoleadmin.zer0balance.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter) {
        LocalDate now = LocalDate.now();
        //if startdate in payload -> TAKE , else month 1st
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : now.withDayOfMonth(1);
        //if enddate in payload -> TAKE , else month last
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : now.withDayOfMonth(now.lengthOfMonth());
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date"; //date def
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        if("income".equalsIgnoreCase(filter.getType())){
            List<IncomeDTO> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomes);
        } else if("expense".equalsIgnoreCase(filter.getType())){
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenses);
        } else{
            return ResponseEntity.badRequest().body("Invalid Type, Must be 'income' or 'expense'");
        }

    }
}
