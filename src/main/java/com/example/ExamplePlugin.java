package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "HP Testing"
)
public class ExamplePlugin extends Plugin
{
	private HashMap<Integer, ArrayList<Hitsplat>> hitsplats;

	@Inject
	private Client client;

	@Override
	protected void startUp() throws Exception
	{
		this.hitsplats = new HashMap<>();
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.hitsplats = null;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		for (Map.Entry<Integer, ArrayList<Hitsplat>> entry : this.hitsplats.entrySet())
		{
			Integer npcId = entry.getKey();
			ArrayList<Hitsplat> hitsplats = entry.getValue();

			StringBuilder hitsplatString = new StringBuilder();
			for (int i = 0; i < hitsplats.size(); i++)
			{
				Hitsplat hitsplat = hitsplats.get(i);
				hitsplatString.append(hitsplat.getAmount());
				if (i + 1 < hitsplats.size())
					hitsplatString.append(",");
			}

			log.debug("NPC {}: {}", npcId, hitsplatString);
		}

		this.hitsplats.clear();
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		Hitsplat hitsplat = event.getHitsplat();
		if (!hitsplat.isMine())
		{
			return;
		}

		Actor actor = event.getActor();
		if (actor instanceof NPC)
		{
			NPC npc = (NPC) actor;

			ArrayList<Hitsplat> hitsplats = this.hitsplats.get(npc.getIndex());
			if (hitsplats == null)
			{
				hitsplats = new ArrayList<>();
			}

			hitsplats.add(hitsplat);
			this.hitsplats.put(npc.getIndex(), hitsplats);
		}
	}
}
