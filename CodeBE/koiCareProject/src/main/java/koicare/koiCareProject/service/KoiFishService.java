package koicare.koiCareProject.service;

import koicare.koiCareProject.dto.request.KoiFishRequest;
import koicare.koiCareProject.entity.*;
import koicare.koiCareProject.exception.AppException;
import koicare.koiCareProject.exception.ErrorCode;
import koicare.koiCareProject.repository.KoiFishRepository;
import koicare.koiCareProject.repository.KoiVarietyRepository;
import koicare.koiCareProject.repository.MemberRepository;
import koicare.koiCareProject.repository.PondRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KoiFishService {

    @Autowired
    KoiFishRepository koiFishRepository;

    @Autowired
    PondRepository pondRepository;

    @Autowired
    KoiVarietyRepository koiVarietyRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    MemberRepository memberRepository;

    //tạo cá koi
    public KoiFish createKoiFish(KoiFishRequest request) {

        KoiFish newKoiFish = new KoiFish();

        Pond pond = pondRepository.getPondByPondID(request.getPondID());

        if (pond != null) {
            newKoiFish.setKoiSex(request.getKoiSex());
            newKoiFish.setKoiName(request.getKoiName());
            newKoiFish.setImage(request.getImage());
            newKoiFish.setBirthday(request.getBirthday());
            newKoiFish.setKoiVariety(koiVarietyRepository.getKoiVarietyByKoiVarietyID(request.getKoiVarietyID()));
            newKoiFish.setPond(pondRepository.getPondByPondID(request.getPondID()));

            Account account = authenticationService.getCurrentAccount();
            Member member = memberRepository.getMemberByAccount(account);
            newKoiFish.setMember(member);

            //sau khi tạo cá sẽ tăng số lượng cá trong hồ lên 1
            //Pond pond = pondRepository.getPondByPondID(request.getPondID());
            pond.setAmountFish(pond.getAmountFish() + 1);
            pondRepository.save(pond);
            return koiFishRepository.save(newKoiFish);
        }
        else{
            throw new AppException(ErrorCode.POND_NOT_EXISTED);
        }


    }

    //lấy lên danh sách cá koi theo MemberID
    public List<KoiFish> getKoiFishes() {

        Account account = authenticationService.getCurrentAccount();
        Member member = memberRepository.getMemberByAccount(account);

        return koiFishRepository.findAllByMember(member);
    }

    //lấy cá koi theo koiFishID
    public KoiFish getKoiFish(long koiFishID) {
        KoiFish koiFish = koiFishRepository.getKoiFishByKoiFishID(koiFishID);
        if (koiFish != null)
            return koiFish;
        else
            throw new AppException(ErrorCode.KOIFISH_NOT_EXISTED);
    }

    //update cá koi
    public KoiFish updateKoiFish(long koiFishID, KoiFishRequest request) {
        KoiFish koiFish = koiFishRepository.getKoiFishByKoiFishID(koiFishID);
        if (koiFish != null) {
            koiFish =  modelMapper.map(request, KoiFish.class);
            koiFish.setKoiFishID(koiFishID);

            Account account = authenticationService.getCurrentAccount();
            Member member = memberRepository.getMemberByAccount(account);
            koiFish.setMember(member);

            return koiFishRepository.save(koiFish);
        } else
            throw new AppException(ErrorCode.KOIFISH_NOT_EXISTED);
    }

    //xóa cá khỏi danh sách
    public void deleteKoiFish(long koiFishID){
        KoiFish koiFish = koiFishRepository.getKoiFishByKoiFishID(koiFishID);

        if (koiFish != null) {
            Pond pond = pondRepository.getPondByPondID(koiFish.getPond().getPondID());
            pond.setAmountFish(pond.getAmountFish() - 1);
            koiFishRepository.deleteById(koiFishID);
        }else
            throw new AppException(ErrorCode.KOIFISH_NOT_EXISTED);
    }
}
