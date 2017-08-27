package com.github.igorperikov.heimdallr;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeimdallrStarter {
    public static void main(String[] args) {
        switch (args.length) {
            case 1:
                new HeimdallrNode(Integer.parseInt(args[0])).start();
                break;
            case 3:
                new HeimdallrNode(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2])).start();
                break;
            default:
                log.error("Specify input params!");
                System.exit(1);
        }
    }
}
