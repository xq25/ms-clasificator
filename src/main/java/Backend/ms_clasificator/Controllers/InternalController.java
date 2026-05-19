package Backend.ms_clasificator.Controllers;

import Backend.ms_clasificator.Services.InternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {
    @Autowired
    private InternalService internalService;

    @GetMapping("exist-relation/{userId}")
    public boolean existRelationWithDoctorOrPatient(@PathVariable String userId){
        return this.internalService.existRelation(userId);
    }
}
