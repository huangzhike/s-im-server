package mmp.im.auth.service;

import mmp.im.auth.dao.RecordDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class XService {

    @Autowired
    private RecordDao recordDao;

}
