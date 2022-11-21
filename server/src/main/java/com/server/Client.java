package com.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.Socket;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Client {
    private String name;
    private Long connectionTime;
    private Socket socket;
}
