package com.Tigggle.Service.Transaction;

import com.Tigggle.DTO.Transaction.GoalDto;
import com.Tigggle.Entity.Member;
import com.Tigggle.Entity.Transaction.Goal;
import com.Tigggle.Entity.Transaction.Keywords;
import com.Tigggle.Repository.Transaction.KeywordsRepository;
import com.Tigggle.Repository.Transaction.TransactionGoalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class GoalService {

    private final TransactionGoalRepository goalRepository;
    private final KeywordsRepository keywordsRepository;

    @Transactional(readOnly = true)
    public List<GoalDto> findAllByMember(Member member) {
        List<Goal> goals = goalRepository.findAllByMember(member);
        List<GoalDto> goalDtos = new ArrayList<>();

        for(Goal goal : goals){
            goalDtos.add(convertToDto(goal, member));
        }

        return goalDtos;
    }

    @Transactional(readOnly = true)
    public GoalDto findByIdAndMember(Long id, Member member) {
        Goal goal = goalRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new EntityNotFoundException("해당 목표를 찾을 수 없습니다."));

        return convertToDto(goal, member);
    }

    public Goal createGoal(GoalDto goalDto, Member member) {
        // DTO에서 받은 keywordId로 Keywords 엔티티를 찾습니다.
        Keywords keyword = keywordsRepository.findById(goalDto.getKeywordId())
                .orElseThrow(() -> new EntityNotFoundException("해당 분류를 찾을 수 없습니다."));

        Goal goal = convertFromDto(goalDto, member);

        return goalRepository.save(goal);
    }

    public Goal updateGoal(Long id, GoalDto goalDto, Member member) {
        // 기존 목표를 안전하게 조회합니다.
        Goal goal = goalRepository.findByIdAndMember(id, member).orElseThrow(() -> new IllegalArgumentException("해당하는 목표를 찾을 수 없습니다."));

        // 키워드가 변경되었을 수 있으니 다시 조회합니다.
        Keywords keyword = keywordsRepository.findById(goalDto.getKeywordId())
                .orElseThrow(() -> new EntityNotFoundException("해당 분류를 찾을 수 없습니다."));

        // goal 엔티티의 내용을 DTO의 내용으로 덮어씁니다.
        goal.setKeyword(keyword);
        goal.setDescription(goalDto.getDescription());
        goal.setAmount(goalDto.getAmount());
        goal.setNote(goalDto.getNote());

        // @Transactional 어노테이션 덕분에, 메서드가 끝나면 변경된 내용을 자동으로 DB에 반영(save)해줍니다.
        return goal;
    }

    public void deleteGoals(List<Long> ids, Member member) {
        // Repository에 만들어둔 메서드를 호출하여 안전하게 삭제합니다.
        goalRepository.deleteAllByIdInAndMember(ids, member);
    }

    private Goal convertFromDto(GoalDto dto, Member member){
        if(dto == null)
            return null;

        Goal goal = new Goal();

        goal.setId(dto.getId());
        goal.setKeyword(keywordsRepository.findById(dto.getKeywordId()).orElseThrow(() -> new IllegalArgumentException("키워드를 찾을 수 없었습니다.")));
        goal.setMember(member);
        goal.setNote(dto.getNote());
        goal.setDescription(dto.getDescription());
        goal.setAmount(dto.getAmount());

        return goal;
    }

    private GoalDto convertToDto(Goal goal, Member member){
        if(goal == null)
            return null;

        GoalDto dto = new GoalDto();

        dto.setAmount(goal.getAmount());
        dto.setDescription(goal.getDescription());
        dto.setNote(goal.getNote());
        dto.setKeyword(goal.getKeyword().getMajorKeyword() + " > " + goal.getKeyword().getMinorKeyword());
        dto.setKeywordId(goal.getKeyword().getId());
        dto.setId(goal.getId());

        return dto;
    }
}
