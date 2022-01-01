//thread - o que vai acontecer quando alguém se conectar

import java.net.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SHandler extends Thread {
	Socket ligacao;
	BufferedReader in;
	PrintWriter out;

	public SHandler(Socket ligacao) {
		this.ligacao = ligacao;
                
		try
		{	
			this.in = new BufferedReader (new InputStreamReader(ligacao.getInputStream()));
			
			this.out = new PrintWriter(ligacao.getOutputStream());
		} catch (IOException e) {
			System.out.println("Erro na ligacao: " + e);
			System.exit(1);
		}
	}
	
	public void run() {                
		try {
			System.out.println("Ligacao aceite");
			
			String msg = in.readLine();
			
			StringTokenizer tokens = new StringTokenizer(msg);
			String metodo = tokens.nextToken();
                        
        //consultar          
			if (metodo.equals("consultar")) {
				//fazer get ListService, mas antes tenho de invocar ...
				          
                //NÃO SEI SE O QUE ESTÁ PARA CIMA ESTÁ BEM 
 /*           }     
            else if (metodo.equals("registarSR")) {
                            // Receber e organizar dados do serviço de rede
                            System.out.println("FUCKING MSG: " +  msg);
                            String chave = tokens.nextToken();
                            String desc = tokens.nextToken();
                            String tipoTech = tokens.nextToken();
                            String ip = tokens.nextToken();
                            String portoString = tokens.nextToken();
                            int porto = 0;
                            try{
                            porto = Integer.parseInt(portoString);
                            }
                            catch (NumberFormatException ex){
                            ex.printStackTrace();
                            }
                            String nome = tokens.nextToken();
                            
                            // Verificar dados
                            System.out.println("Dados recebidos:" + chave + " " + desc + " " + tipoTech + " " + ip + " " + porto + " " + nome);
                            
                            // Criar novo serviço
                            WebService novoWS = new WebService(chave, desc, tipoTech, ip, porto, nome);
                            
                            // Definir lista
                            WSList wsList = null;
                            
                            //-------------------
                            // Ler lista guardada
                            //-------------------
                            
                            // Ficheiro da lista
                            File file = new File("C:\\Users\\GL704GW\\Desktop\\1Semestre\\FSD\\FSDProject\\wsList.txt");
                                                       
                            try {
                                // Se o ficheiro estiver vazio então criamos novo objeto da classe WBList e é lançada a exeption
                                if (file.length() == 0)
                                {
                                    wsList = new WSList();
                                }
                                else{
                                // Cria as streams e vai buscar a lista que já existe e associa à variável wsList
                                    try {
                                        FileInputStream fi = new FileInputStream(file);
                                        ObjectInputStream oi = new ObjectInputStream(fi);
                                        wsList = (WSList) oi.readObject();
                                        oi.close();
                                    } catch (ClassNotFoundException ex) {
                                        Logger.getLogger(STRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                
                            } catch (EOFException e){
                                System.out.println("ficheiro vazio");
                            }
                            
                            //-------------------
                            // Adicionar serviço à lista e guardar
                            //-------------------
                            
                            // Ficheiro da lista
                            file = new File("C:\\Users\\GL704GW\\Desktop\\1Semestre\\FSD\\FSDProject\\wsList.txt");
                            
                            // Adiciona o novo serviço
                            wsList.add(novoWS);
                            
                            // Guardar estado do objeto (lista)
                            FileOutputStream f = new FileOutputStream(file);
			    ObjectOutputStream o = new ObjectOutputStream(f);
    
			    // Guardar estado do objeto (lista);
			    o.writeObject(wsList);

			    o.close();
			    f.close();
                             
                            //print dos serviços existentes
                            for(WebService w : wsList.getWebServiceList()) {
                                System.out.println(w.toString());
                            }
                            out.println("Serviço adicionado com sucesso");
                            
			    }
			else 
				out.println("201;method not found");
				
			out.flush();
			in.close();
			out.close();
			ligacao.close();
		} catch (IOException | NoSuchAlgorithmException e) {
			System.out.println("Erro na execucao do servidor: " + e);
			System.exit(1);
		}
	}
}
