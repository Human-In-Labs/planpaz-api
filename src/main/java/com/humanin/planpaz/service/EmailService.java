package com.humanin.planpaz.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	// Lista de frases motivacionais e reflexivas
	private final List<String> frasesMotivacionais = List.of(
			"✨ Frase do Dia: \"Seja puro e transparente como a água corrente. O sol brilha para todos, mas a sombra é só para quem merece!\" 🌊☀️🌳\n\n",
			"✨ Frase do Dia: \"Seja a mudança que você quer ver no mundo.\" 🌍✨\n\n",
			"✨ Frase do Dia: \"Nenhum de nós jamais faz grandes coisas sozinho. Mas todos podemos fazer pequenas coisas com grande amor, e juntos podemos fazer algo maravilhoso!\" 🤝💖🌱\n\n",

			"✨ Frase do Dia: \"Quem não sabe cuidar da raiz, não merece colher os frutos nem se abrigar na sombra.\" 🌳🍎\n\n",
			"✨ Frase do Dia: \"A tempestade que assusta o fraco é a mesma água que fortalece quem tem raiz profunda.\" 🌧️⚡💪\n\n",
			"✨ Frase do Dia: \"O tempo não apressa a semente, e a terra não cobra o tempo. Respeite o seu próprio processo.\" ⏳🌱✨\n\n",
			"✨ Frase do Dia: \"Não adiantar regar o vaso se o solo não absorve. Cuide  do coração antes de exigir frutos.\" 🫀💧🪴\n\n",
			"✨ Frase do Dia: \"A flor não compete com a do lado, ela simplesmente floresce. Seja você mesmo.\" 🌸🌿\n\n",

			"✨ Frase do Dia: \"Cultivar uma planta é cultivar a esperança de um amanhã mais verde!\" 🌸\n\n",
			"✨ Frase do Dia: \"Pequenos gestos de cuidado diário geram grandes floradas no futuro.\" 🌿\n\n",
			"✨ Frase do Dia: \"A paciência é a chave para ver suas sementes transformarem-se em vida!\" 🌻\n\n",
			"✨ Frase do Dia: \"Assim como as plantas, nós também precisamos de tempo, sol e afeto para florescer.\" ☀️\n\n",
			"✨ Frase do Dia: \"Regar suas plantas é também um momento para desacelerar e cultivar a paz interior.\" 🧘‍♀️🌱\n\n",
			"✨ Frase do Dia: \"Cada folha nova é uma vitória da sua dedicação!\" 🍃🎉\n\n",
			"✨ Frase do Dia: \"O amor e a atenção que você dedica hoje voltam em forma de natureza viva.\" 💚🌷\n\n");

	//Sorteia uma frase aleatória da lista.
	 
	private String obterFraseAleatoria() {
		int index = ThreadLocalRandom.current().nextInt(frasesMotivacionais.size());
		return frasesMotivacionais.get(index);
	}

	public void enviarAlertaRega(String para, String nomePlanta, String statusRega) {

		String fraseDoDia = obterFraseAleatoria();
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(para);
		message.setSubject("🌱 PlanPaz | Sua planta precisa de você!");

		message.setText("Olá! 🌿\n\n" +

				"Sua plantinha \"" + nomePlanta + "\" está precisando de um pouco de atenção. 💚\n\n" +

				"💧 Status da rega: " + statusRega + "\n\n" +

				"Que tal reservar alguns minutinhos para cuidar dela? "
				+ "Pequenos cuidados fazem toda a diferença para que ela continue crescendo forte e saudável. 🌱✨\n\n" +

				fraseDoDia +

				"O PlanPaz está aqui para ajudar você nessa jornada! 💚\n\n" +

				"Até logo! 🌱\n" + "Equipe PlanPaz");

		mailSender.send(message);
	}
}