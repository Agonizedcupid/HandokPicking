package com.aariyan.pickingplan.Interface;

import java.util.List;

public interface GetTicketInterface {
    void getTicket(List<String> listOfTicket);
    void error(String errorMessage);
}
