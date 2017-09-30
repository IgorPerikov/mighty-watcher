package com.github.igorperikov.heimdallr;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeimdallrStarter {
    public static void main(String[] args) throws Exception {
        switch (args.length) {
            case 2:
                new HeimdallrNode(
                        Integer.parseInt(args[0]),
                        Integer.parseInt(args[1])
                ).start();
                break;
            case 4:
                new HeimdallrNode(
                        Integer.parseInt(args[0]),
                        Integer.parseInt(args[1]),
                        args[2],
                        Integer.parseInt(args[3])
                ).start();
                break;
            default:
                log.error("Specify input params!");
                System.exit(1);
        }
    }
}
