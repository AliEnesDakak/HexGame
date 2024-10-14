package com.example.grup32;

import com.example.group32.GameLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Modality;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HexGameController extends HexGameFX
{
    @FXML
    private GridPane gridPane;
    @FXML
    private Label turnLabel;
    public String turnText;
    private int gridSize = 5;
    private int turn = 1;
    private GameLogic redMap;
    private GameLogic blueMap;
    private final Map<String, Polygon> hexagonMap;
    private boolean gameOver;

    public HexGameController()
    {
        //Maplerin olusturuldugu fonksiyon
        redMap = new GameLogic();
        blueMap = new GameLogic();
        hexagonMap = new HashMap<>();
        gameOver = false;
    }

    private void CreateGrid() {
        Pane pane = new Pane();
        double hexagonWidth = 30.0 * Math.sqrt(3);
        double hexagonHeight = 30.0 * 2;
        double offset = hexagonHeight * 0.44;

        for (int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                Polygon hexagon = CreateHexagon(i, j);
                String kenarlar = i + "," + j;
                hexagonMap.put(kenarlar, hexagon);

                redMap.makeSetMap(kenarlar);
                blueMap.makeSetMap(kenarlar);

                double x = j * hexagonWidth;
                double y = i * hexagonHeight * 0.75;
                x += i * offset;

                hexagon.setTranslateX(x);
                hexagon.setTranslateY(y);
                pane.getChildren().add(hexagon);
                hexagon.setStroke(Color.BLACK);
                hexagon.setStrokeWidth(2.0);

                /*
                    Renkle alakalı ilk denememiz ancak tam olarak istediğimizi elde edemeyince drawBorders metodunu yazdık
                if(j == 0 || j == gridSize -1)
                {
                    hexagon.setStroke(Color.BLUE);
                }
                else if (i == 0 || i == gridSize-1)
                {
                    hexagon.setStroke(Color.RED);
                }
                else
                {
                    hexagon.setStroke(Color.BLACK);
                }
                hexagon.setStrokeWidth(2.0);
                */

            }
        }

        // Grid etrafına sınır çizgileri ekleyin
        drawBorders(pane, hexagonWidth, hexagonHeight, offset,gridSize);

        redMap.makeSetMap("-1,0");
        redMap.makeSetMap(gridSize + ",0");
        blueMap.makeSetMap("0,-1");
        blueMap.makeSetMap("0," + gridSize);

        for (int i = 0; i < gridSize; i++) {
            redMap.MapControl("-1,0", "0," + i);
            redMap.MapControl(gridSize + ",0", (gridSize - 1) + "," + i);
        }

        for (int i = 0; i < gridSize; i++) {
            blueMap.MapControl("0,-1", i + ",0");
            blueMap.MapControl("0," + gridSize, i + "," + (gridSize - 1));
        }

        gridPane.getChildren().clear();
        gridPane.getChildren().add(pane);
    }

    private void drawBorders(Pane pane, double hexagonWidth, double hexagonHeight, double offset, int gridSize) {
        // Oranlama faktörü
        double scaleFactor = 0.12 * (gridSize - 5);

        // Köşe noktalarının hesaplanması
        double[] topLeft = {-0.5 * hexagonWidth - scaleFactor, -3 * hexagonHeight / 6 - scaleFactor};
        double[] topRight = {(gridSize - 0.5) * hexagonWidth + (gridSize - 1) * offset + scaleFactor, -3 * hexagonHeight / 6 - scaleFactor};
        double[] bottomRight = {(gridSize - 0.5) * hexagonWidth + (gridSize - 1) * offset + scaleFactor, (gridSize - 1) * hexagonHeight * 0.75 + hexagonHeight / 2 + scaleFactor};
        double[] bottomLeft = {-0.5 * hexagonWidth - scaleFactor, (gridSize - 1) * hexagonHeight * 0.75 + hexagonHeight / 2 + scaleFactor};

        // Üst kenar çizgisi
        Line topBorder = new Line(topLeft[0], topLeft[1], topRight[0], topRight[1]);
        topBorder.setStroke(Color.RED);
        topBorder.setStrokeWidth(3.0);
        pane.getChildren().add(topBorder);

        // Sağ kenar çizgisi
        Line rightBorder = new Line(topRight[0], topRight[1], bottomRight[0], bottomRight[1]);
        rightBorder.setStroke(Color.BLUE);
        rightBorder.setStrokeWidth(3.0);
        pane.getChildren().add(rightBorder);

        // Alt kenar çizgisi
        Line bottomBorder = new Line(bottomRight[0], bottomRight[1], bottomLeft[0], bottomLeft[1]);
        bottomBorder.setStroke(Color.RED);
        bottomBorder.setStrokeWidth(3.0);
        pane.getChildren().add(bottomBorder);

        // Sol kenar çizgisi
        Line leftBorder = new Line(bottomLeft[0], bottomLeft[1], topLeft[0], topLeft[1]);
        leftBorder.setStroke(Color.BLUE);
        leftBorder.setStrokeWidth(3.0);
        pane.getChildren().add(leftBorder);
    }


    private Polygon CreateHexagon(int i, int j)
    {
        //Altigen seklinin olusturulmasi ve renklendirilerek tiklama olayinin olusturuldugu fonksiyon
        Polygon hexagon = new Polygon();
        double size = 30.0;

        for (int k = 0; k < 6; ++k)
        {
            double angle = Math.PI / 3.0 * ((double) k + 0.5);
            double x = size * Math.cos(angle);
            double y = size * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }

        hexagon.setFill(Color.LIGHTGRAY);
        hexagon.setUserData(i + "," + j);

        hexagon.setOnMouseClicked(event ->
        {
            if (hexagon.getUserData() != null)
            {
                String[] pos = ((String) hexagon.getUserData()).split(",");
                handleHexagonClick(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
            }
        });

        return hexagon;
    }

    private void handleHexagonClick(int i, int j)
    {
        //Oyunun haritasindaki secilmis olan altigenleri kontrol edilerek oyunun ilerleyisini kontrol eden fonksiyon
        if (gameOver) return;

        String kenarlar = i + "," + j;
        Polygon hexagon = hexagonMap.get(kenarlar);
        if (hexagon.getFill().equals(Color.LIGHTGRAY))
        {
            String playerColor;
            if (turn % 2 == 1)
            {
                hexagon.setFill(Color.RED);
                playerColor = "red";
                turnText = "BLUE";
            }
            else
            {
                hexagon.setFill(Color.BLUE);
                playerColor = "blue";
                turnText = "RED";
            }

            GamePlay(i, j, playerColor);
            ++turn;
            turnLabel.setText("Turn: " + turnText);

            if (CheckWinner(playerColor))
            {
                turnLabel.setText(playerColor.substring(0, 1).toUpperCase() + playerColor.substring(1) + " won!");
                gameOver = true;
                WinnerPanel(playerColor);
            }
        }
    }

    private void GamePlay(int i, int j, String player)
    {
        //Oyunun haritasindaki secilmis olan altigenleri kontrol edilerek oyunun bitip bitmedigini kontrol eden fonksiyon
        String currentCell = i + "," + j;
        String[][] neighbors = {
                {i + 1 + "", j + ""},
                {i + 1 + "", j - 1 + ""},
                {i + "", j + 1 + ""},
                {i + "", j - 1 + ""},
                {i - 1 + "", j + ""},
                {i - 1 + "", j + 1 + ""}
        };

        for (String[] neighbor : neighbors) {
            int neighbor_i = Integer.parseInt(neighbor[0]);
            int neighbor_j = Integer.parseInt(neighbor[1]);

            String neighborCell = neighbor_i + "," + neighbor_j;

            if (0 <= neighbor_i && neighbor_i < gridSize && 0 <= neighbor_j && neighbor_j < gridSize)
            {
                Polygon neighborHex = hexagonMap.get(neighborCell);
                if (player.equals("red") && neighborHex.getFill().equals(Color.RED))
                {
                    redMap.MapControl(neighborCell, currentCell);
                }
                else if (player.equals("blue") && neighborHex.getFill().equals(Color.BLUE))
                {
                    blueMap.MapControl(neighborCell, currentCell);
                }
            }
        }
    }

    private boolean CheckWinner(String player)
    {
        //Kimin kazandigini kontrol eden fonksiyon
        if (player.equals("red"))
        {
            return redMap.findWinner("-1,0").equals(redMap.findWinner(gridSize + ",0"));
        }
        else
        {
            return blueMap.findWinner("0,-1").equals(blueMap.findWinner("0," + gridSize));
        }
    }

    private void WinnerPanel(String player)
    {
        //Oyun sonu ekrani fonksiyonu
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(gridPane.getScene().getWindow());

        if (player.equals("red"))
        {
            alert.setHeaderText("Kazanan: Kırmızı");
            alert.setContentText("Her kaybettiğinde kazanmak için yeni bir sebep bulursun.");
        }
        else
        {
            alert.setHeaderText("Kazanan: Mavi");
            alert.setContentText("Her kaybettiğinde kazanmak için yeni bir sebep bulursun.");
        }

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK)
        {
            GameReset();
            alert.close();
        }
    }
    @FXML
    private void StartGame()
    {
        //Oyunu baslatan fonksiyon
        GameReset();
    }

    @FXML
    private void SizeSelector(ActionEvent event)
    {
        //Oyunun haritasinin boyutunu ayarlayan fonksiyon
        String selectedSize = ((Button) event.getSource()).getText();
        gridSize = Integer.parseInt(selectedSize.split(" ")[0]);
        CreateGrid();
        GameReset();
    }

    private void GameReset()
    {
        //Oyunun haritasini temizleyip bastan olusturan fonksiyon
        turn = 1;
        redMap = new GameLogic();
        blueMap = new GameLogic();
        hexagonMap.clear();
        gameOver = false;
        CreateGrid();
    }
}
