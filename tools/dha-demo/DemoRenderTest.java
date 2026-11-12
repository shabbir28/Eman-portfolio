import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
public class DemoRenderTest extends Application {
 static void save(Parent root,String filename)throws Exception{
  root.applyCss();root.layout();var image=root.snapshot(null,null);int w=(int)image.getWidth(),h=(int)image.getHeight();var out=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);var reader=image.getPixelReader();for(int y=0;y<h;y++)for(int x=0;x<w;x++)out.setRGB(x,y,reader.getArgb(x,y));ImageIO.write(out,"png",Path.of("assets",filename).toFile());
 }
 public void start(Stage stage){try{
  database.ConnectionFactory.getInstance();
  Parent root=FXMLLoader.load(getClass().getResource("/view/base.fxml"));stage.setScene(new Scene(root,1280,800));stage.show();save(root,"dha-dashboard.png");
  String[] buttons={"storeInventoryButton","userInventoryButton","auctionInventoryButton","reportButton","lowStockButton","dhaEmployeeButton","addItemButton","assignItemButton","returnItemButton"};
  for(String id:buttons){((Button)root.lookup("#"+id)).fire();root.applyCss();root.layout();if(id.equals("storeInventoryButton"))save(root,"dha-inventory.png");if(id.equals("storeInventoryButton")) { for(String t:new String[]{"btnReceived","btnTemporary","btnPermanent","btnStore"}) { ((ToggleButton)root.lookup("#"+t)).fire();root.applyCss();root.layout(); } } System.out.println("LOADED "+id);}
  for(String file:new String[]{"add_store_item","add_user_item","add_auction_item","assign_store_item","assign_user_item","assign_auction_item","low_stock_add","low_stock_view","dha_employee_add","dha_employee_view"}) {Parent form=FXMLLoader.load(getClass().getResource("/view/"+file+".fxml"));stage.setScene(new Scene(form));form.applyCss();form.layout();System.out.println("LOADED "+file);}
  Parent login=FXMLLoader.load(getClass().getResource("/view/login.fxml"));stage.setScene(new Scene(login));save(login,"dha-login.png");
  System.out.println("PASS: JavaFX screens loaded and screenshots rendered");Platform.exit();
 }catch(Throwable e){e.printStackTrace();System.exit(1);}}
 public static void main(String[] args){launch(args);}
}