package Backend.ms_clasificator.Services;

import Backend.ms_clasificator.DTOs.EvaluationArea.EvaluationAreaCreateDTO;
import Backend.ms_clasificator.Models.EvaluationArea;
import Backend.ms_clasificator.Repositories.EvaluationAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationAreaService {

    @Autowired
    private EvaluationAreaRepository evaluationAreaRepository;


    public List<EvaluationArea> findAll() {
        return evaluationAreaRepository.findAll();
    }

    public EvaluationArea findById(Integer id){
        return evaluationAreaRepository.findById(id).orElse(null);
    }

    public EvaluationArea create(EvaluationAreaCreateDTO evaluationAreaCreateDTO){
        return null;
    }
}
