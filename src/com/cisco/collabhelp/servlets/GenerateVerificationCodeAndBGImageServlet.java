package com.cisco.collabhelp.servlets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Project Name: WebexDocsWeb
 * Title: GenerateVerificationCodeServlet.java
 * Description: Generate verification code and background image.
 * Company: Cisco
 * Copyright: ©2018 Cisco and/or its affiliates
 * @author Adam Kong
 * @date 20 Sep 2018
 * @version 1.0
 */

public class GenerateVerificationCodeAndBGImageServlet extends HttpServlet {

	// The chars of the graphic verification code. It will pick up some chars in it as verification code by random.
    private static String codeChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the length of the graphci verification code.
        int charsLength = codeChars.length();
        // close client browsers' cache. Because the different browsers' version, we use the below sentence to do it for all browsers/versions.
        response.setHeader("ragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // set the width and height of the background picture.
        int width = 90, height = 30;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Random random = new Random();
        g.setColor(getRandomColor(180, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ITALIC, height));
        g.setColor(getRandomColor(120, 180));
        
        // save the generated verification code.
        StringBuffer validationCode = new StringBuffer();
        // The random fonts of verification code.
        String[] fontNames = { "Times New Roman", "Book antiqua", "Arial" };

        // generate a 4-digit/letter verification code.
        for (int i = 0; i < 4; i++) {
        	// set the font of verification code by random
            g.setFont(new Font(fontNames[random.nextInt(3)], Font.ITALIC, height));
            // get the chars of verification code by random
            char codeChar = codeChars.charAt(random.nextInt(charsLength));
            validationCode.append(codeChar);
            // set the color of the selected char
            g.setColor(getRandomColor(10, 100));
            // display the char of verification code. x and y are produced by random.
            g.drawString(String.valueOf(codeChar), 16 * i + random.nextInt(7), height - random.nextInt(6));
        }

        HttpSession session = request.getSession();
        // session.setMaxInactiveInterval(5 * 60);
        session.setAttribute("verifyCode", validationCode.toString());
        // close the Graphics object.
        g.dispose();

        OutputStream outS = response.getOutputStream();
        ImageIO.write(image, "JPEG", outS);
    }

    private Color getRandomColor(int minColor, int maxColor) {
          Random random = new Random();
            if(minColor > 255){
                minColor = 255;
            }
            if(maxColor > 255){
                maxColor = 255;
            }
            // get the value of r's random color.
            int red = minColor+random.nextInt(maxColor-minColor);
            int green = minColor + random.nextInt(maxColor-minColor);
            int blue = minColor + random.nextInt(maxColor-minColor);
            return new Color(red,green,blue);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
