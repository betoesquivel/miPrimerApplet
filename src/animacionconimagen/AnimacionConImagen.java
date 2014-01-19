/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package animacionconimagen;

/**
 *
 * @author ppesq
 */
/**
 * Una clase para ejemplificar la animacion de una imagen
 *
 * Animacion <code>Applet</code> application
 *
 * @author José Alberto Esquivel
 * @version 1.00 2008/6/10
 */
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Toolkit;
import java.net.URL;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;

public class AnimacionConImagen extends Applet implements Runnable, KeyListener {

    private static final long serialVersionUID = 1L;
    // Se declaran las variables.
    private int x_pos;			// Posicion x del elefante
    private int y_pos;			// Posicion y del elefante

    private int velocidad;

    private Image dbImage;
    private Graphics dbg;

    private Image elefante;                 // Imagen del elefante
    private final URL eURL_derecha = this.getClass().getResource("/Imagenes/elefante.gif");
    private final URL eURL_izquierda = this.getClass().getResource("/Imagenes/elefante-izquierda.gif");
    private final URL eaURL = this.getClass().getResource("/sonidos/elephant.wav");
    private final int largo_elefante = 89;
    private final int altura_elefante = 73;
    private int direccion;              //1 = arriba; 2 = abajo; 3 = izquierda; 4 = derecha; 
    private boolean cambio_imagen;      //Es true cuando tengo que cambiar la imagen en mi método de paint.
    private int contador_colision;      //Contiene la cantidad de ciclos en pausa que tiene que estar la imagen al colisionar.
    private boolean en_colision;        //Es true cuando estoy en colisión.
    private final int ciclos_de_espera_colision = 25; //contiene los ciclos de paint() que me voy a esperar al colisionar
    private AudioClip sonido;

    /**
     * Metodo <I>init</I> sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inizializan las variables o se crean los objetos y se
     * ejecuta una sola vez cuando inicia el <code>Applet</code>.
     */
    public void init() {
        x_pos = (int) (Math.random() * (getWidth() / 4));    // posicion en x es un cuarto del applet;
        y_pos = (int) (Math.random() * (getHeight() / 4));    // posicion en y es un cuarto del applet

        elefante = Toolkit.getDefaultToolkit().getImage(eURL_derecha);
//        largo_elefante = elefante.getIconWidth();
//        altura_elefante = elefante.getIconHeight();

        sonido = getAudioClip(eaURL);
        setBackground(Color.yellow);

        direccion = 4;
        velocidad = 1;
        cambio_imagen = true;
        en_colision = false;
        contador_colision = -1;

        addKeyListener(this);
    }

    /**
     * Metodo <I>start</I> sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo para la animacion este metodo
     * es llamado despues del init o cuando el usuario visita otra pagina y
     * luego regresa a la pagina en donde esta este <code>Applet</code>
     *
     */
    public void start() {
        // Declaras un hilo
        Thread th = new Thread(this);
        // Empieza el hilo
        th.start();
    }

    /**
     * Metodo <I>stop</I> sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se pueden tomar acciones para cuando se termina de usar el
     * <code>Applet</code>. Usualmente cuando el usuario sale de la pagina en
     * donde esta este <code>Applet</code>.
     */
    public void stop() {

    }

    /**
     * Metodo <I>destroy</I> sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se toman las acciones necesarias para cuando el
     * <code>Applet</code> ya no va a ser usado. Usualmente cuando el usuario
     * cierra el navegador.
     */
    public void destroy() {

    }

    /**
     * Metodo <I>run</I> sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, es un ciclo indefinido donde se
     * incrementa la posicion en x, se repinta el <code>Applet</code> y luego
     * manda a dormir el hilo.
     *
     */
    public void run() {
        while (true) {
            // Se actualiza la posicion del elefante
            actualiza();

            // Reviso si hay 
            checaColision();
            // Se actualiza el <code>Applet</code> repintando el contenido
            repaint();

            try {
                // El thread se duerme
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                System.out.println("Error en " + ex.toString());
            }
        }
    }

    /**
     * Metodo <I>paint</I> sobrescrito de la clase <code>Applet</code>, heredado
     * de la clase Container.<P>
     * En este metodo se dibuja la imagen con la posicion actualizada, ademas
     * que cuando la imagen es cargada te despliega una advertencia.
     *
     * @param g es el <code>objeto grafico</code> usado para dibujar.
     */
    public void paint(Graphics g) {
        if (elefante != null) {
            //Dibuja la imagen en la posicion actualizada
            if (cambio_imagen) {
                if (en_colision) {
                    //cambio el objeto de elefante a su imagen de colision
                } else {
                    if (direccion == 4) {
                        elefante = Toolkit.getDefaultToolkit().getImage(eURL_derecha);
                    } else {
                        elefante = Toolkit.getDefaultToolkit().getImage(eURL_izquierda);
                    }
                }
                cambio_imagen = false;
            }
            g.drawImage(elefante, x_pos, y_pos, this);

        } else {
            //Da un mensaje mientras se carga el dibujo	
            g.drawString("Estoy cargando..", 10, 10);
        }

    }

    public void update(Graphics g) {

        // Inicializan el DoubleBuffer
        if (dbImage == null) {

            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }
        // Actualiza la imagen de fondo
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);
        // Actualiza el Foreground
        dbg.setColor(getForeground());
        paint(dbg);
        // Dibuja la imagen actualizada y con esto ya no se ve parpadeo
        g.drawImage(dbImage, 0, 0, this);
    }

    public void checaColision() {
        // Detengo al elefante cuando choca con la pared.
        if (x_pos == (getWidth() - largo_elefante) || x_pos == 0
                || y_pos == (getHeight() - altura_elefante) || y_pos == 0) {
            sonido.play();
            velocidad = 0;
            if (!en_colision) {
                contador_colision = ciclos_de_espera_colision;
                cambio_imagen = true;
                en_colision = true;
            } else {
                contador_colision--;
                if (contador_colision == -1) {
                    cambio_imagen = true;
                    en_colision = false;
                    velocidad = 1;
                    invertirDireccion();
                }
            }

        }
    }

    public void actualiza() {
        // Si no esta en colision, Se actualiza la posicion del elefante. 
        if (!en_colision) {
            switch (direccion) {
                case 1:
                    y_pos -= velocidad;
                    break;
                case 2:
                    y_pos += velocidad;
                    break;
                case 3:
                    x_pos -= velocidad;
                    break;
                case 4:
                    x_pos += velocidad;
                    break;

            }
        }
    }

    public void keyPressed(KeyEvent e) {

        //Presiono flecha arriba
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (direccion == 2 && velocidad > 0) {
                velocidad--;
            } else if (direccion == 1) {
                velocidad++;
            } else {
                velocidad = 1;
                direccion = 1;
            }
            //Presiono flecha abajo
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (direccion == 1 && velocidad > 0) {
                velocidad--;
            } else if (direccion == 2) {
                velocidad++;
            } else {
                velocidad = 1;
                direccion = 2;
            }
            //Presiono flecha izquierda
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (direccion == 4 && velocidad > 0) {
                velocidad--;
            } else if (direccion == 3) {
                velocidad++;
            } else {
                velocidad = 1;
                direccion = 3;
                cambio_imagen = true;
            }
            //Presiono flecha derecha
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (direccion == 3 && velocidad > 0) {
                velocidad--;
            } else if (direccion == 4) {
                velocidad++;
            } else {
                velocidad = 1;
                direccion = 4;
                cambio_imagen = true;
            }
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {
    }

    public void invertirDireccion() {
        switch (direccion) {
            case 1:
                direccion = 2;
                break;
            case 2:
                direccion = 1;
                break;
            case 3:
                direccion = 4;
                break;
            case 4:
                direccion = 3;
                break;
        }
    }
}
