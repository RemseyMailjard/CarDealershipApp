package com.skills4it.dealership.data.dao;



import com.skills4it.dealership.model.LeaseContract;
import java.util.List;

public interface LeaseContractDao {
    LeaseContract create(LeaseContract contract);
    List<LeaseContract> getAll();
}
