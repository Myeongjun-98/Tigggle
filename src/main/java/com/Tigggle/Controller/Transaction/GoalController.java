// com/Tigggle/Controller/GoalController.java

package com.Tigggle.Controller.Transaction;

import com.Tigggle.DTO.Transaction.GoalDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Goal;
import com.Tigggle.Repository.Transaction.KeywordsRepository;
import com.Tigggle.Repository.UserRepository;
import com.Tigggle.Service.Transaction.GoalService; // 🚨 다음 단계에서 만들 서비스입니다.
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다.
public class GoalController {

    private final GoalService goalService; // 비즈니스 로직을 처리할 서비스
    private final KeywordsRepository keywordsRepository;
    private final UserRepository memberRepository;

    @GetMapping("/transaction/goals")
    public String goalManagementPage(Model model){
        model.addAttribute("keywords", keywordsRepository.findAll());
        return "/transaction/goals";
    }

    @GetMapping("/transaction/goals/api")
    @ResponseBody
    public ResponseEntity<List<GoalDto>> getAllGoals(Principal principal) {
        Member member = memberRepository.findByAccessId(principal.getName());

        List<GoalDto> goals = goalService.findAllByMember(member);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/transaction/goals/api/{id}")
    @ResponseBody
    public ResponseEntity<GoalDto> getGoalById(@PathVariable Long id, Principal principal) {
        Member member = memberRepository.findByAccessId(principal.getName());

        GoalDto goal = goalService.findByIdAndMember(id, member);
        return ResponseEntity.ok(goal);
    }

    @PostMapping("/transaction/goals/api")
    @ResponseBody
    public ResponseEntity<Goal> createGoal(@RequestBody GoalDto goalDto, Principal principal) {
        Member member = memberRepository.findByAccessId(principal.getName());

        Goal createdGoal = goalService.createGoal(goalDto, member);
        return ResponseEntity.ok(createdGoal);
    }

    @PutMapping("/transaction/goals/api/{id}")
    @ResponseBody
    public ResponseEntity<Goal> updateGoal(@PathVariable Long id, @RequestBody GoalDto goalDto, Principal principal) {
        Member member = memberRepository.findByAccessId(principal.getName());

        Goal updatedGoal = goalService.updateGoal(id, goalDto, member);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/transaction/goals/api")
    @ResponseBody
    public ResponseEntity<Void> deleteGoals(@RequestBody List<Long> ids, Principal principal) {
        Member member = memberRepository.findByAccessId(principal.getName());

        goalService.deleteGoals(ids, member);
        return ResponseEntity.ok().build(); // 성공적으로 처리되었으나 반환할 본문이 없음을 의미
    }
}